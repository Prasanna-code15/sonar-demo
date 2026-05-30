# =============================================================================
# main.tf — Sample Terraform script with intentional security issues
# For use with SonarQube / tfsec / Checkov scanning demos
# DO NOT use these patterns in production
# =============================================================================

terraform {
  required_version = ">= 1.5.0"
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

# =============================================================================
# Provider — Security Issue 1: Hard-coded AWS credentials
# tfsec: AWS004 | Sonar: S6275
# Fix: Use environment variables, IAM roles, or AWS Secrets Manager
# =============================================================================
provider "aws" {
  region     = "us-east-1"
  access_key = "AKIAIOSFODNN7EXAMPLE"           # hard-coded AWS access key
  secret_key = "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY"  # hard-coded secret
}

# =============================================================================
# Variables
# =============================================================================
variable "environment" {
  description = "Deployment environment"
  type        = string
  default     = "dev"
}

variable "db_name" {
  description = "RDS database name"
  type        = string
  default     = "appdb"
}

# =============================================================================
# Security Issue 2: Hard-coded DB password in variable default
# tfsec: AWS011 | Sonar: S2068
# Fix: Use AWS Secrets Manager or mark sensitive = true with no default
# =============================================================================
variable "db_password" {
  description = "RDS master password"
  type        = string
  default     = "Admin@1234!"    # hard-coded password in plain text
  # sensitive = true             # this line is intentionally commented out
}

# =============================================================================
# Security Issue 3: Hard-coded API key in locals
# Sonar: S2068
# Fix: Read from AWS Secrets Manager or SSM Parameter Store
# =============================================================================
locals {
  app_api_key    = "sk-prod-9a8b7c6d5e4f3a2b1c0d"   # hard-coded API key
  jwt_secret     = "my$uper$ecretJWTkey2024"          # hard-coded JWT secret
  smtp_password  = "Smtp@Pass#99"                     # hard-coded SMTP password
  environment    = var.environment
}

# =============================================================================
# S3 Bucket
# Security Issue 4: S3 bucket with public ACL
# tfsec: AWS017 | Sonar: S6265
# Fix: Set acl = "private" and add bucket public access block
# =============================================================================
resource "aws_s3_bucket" "app_bucket" {
  bucket = "my-app-bucket-${local.environment}"
  acl    = "public-read"   # publicly readable bucket

  tags = {
    Environment = local.environment
    ApiKey      = local.app_api_key   # secret exposed in resource tag
  }
}

# Security Issue 5: S3 bucket versioning disabled
# tfsec: AWS072
resource "aws_s3_bucket_versioning" "app_bucket_versioning" {
  bucket = aws_s3_bucket.app_bucket.id
  versioning_configuration {
    status = "Disabled"   # versioning off — data loss risk
  }
}

# Security Issue 6: No S3 server-side encryption
# tfsec: AWS023
# Fix: Add aws_s3_bucket_server_side_encryption_configuration resource

# =============================================================================
# RDS Instance
# Security Issue 7: DB password passed as plain-text variable
# Security Issue 8: Publicly accessible RDS instance
# Security Issue 9: Storage encryption disabled
# tfsec: AWS052, AWS053
# =============================================================================
resource "aws_db_instance" "app_db" {
  identifier        = "app-db-${local.environment}"
  engine            = "mysql"
  engine_version    = "8.0"
  instance_class    = "db.t3.micro"
  allocated_storage = 20

  db_name  = var.db_name
  username = "admin"
  password = var.db_password    # plain-text password from variable

  publicly_accessible    = true    # DB exposed to the internet
  storage_encrypted      = false   # encryption at rest disabled
  deletion_protection    = false   # accidental deletion possible
  skip_final_snapshot    = true

  tags = {
    Environment = local.environment
  }
}

# =============================================================================
# Security Group
# Security Issue 10: SSH open to the world (0.0.0.0/0)
# tfsec: AWS006 | Sonar: S6321
# Fix: Restrict to known CIDR ranges
# =============================================================================
resource "aws_security_group" "app_sg" {
  name        = "app-sg-${local.environment}"
  description = "Application security group"

  # SSH wide open to the internet
  ingress {
    description = "SSH from anywhere"
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]   # allows SSH from any IP
  }

  # All outbound traffic allowed
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

# =============================================================================
# EC2 Instance
# Security Issue 11: Hard-coded private key in user_data
# Security Issue 12: IMDSv1 allowed (metadata service v1)
# tfsec: AWS058
# =============================================================================
resource "aws_instance" "app_server" {
  ami           = "ami-0c55b159cbfafe1f0"
  instance_type = "t3.micro"

  vpc_security_group_ids = [aws_security_group.app_sg.id]

  # Hard-coded secrets injected into the instance at boot
  user_data = <<-EOF
    #!/bin/bash
    export DB_PASSWORD="Admin@1234!"
    export API_KEY="sk-prod-9a8b7c6d5e4f3a2b1c0d"
    export JWT_SECRET="my$uper$ecretJWTkey2024"
    echo "DB_PASSWORD=$DB_PASSWORD" >> /etc/environment
    echo "API_KEY=$API_KEY"         >> /etc/environment
  EOF

  metadata_options {
    http_endpoint = "enabled"
    http_tokens   = "optional"  # IMDSv1 still allowed; set to "required" for IMDSv2
  }

  tags = {
    Name        = "app-server-${local.environment}"
    Environment = local.environment
  }
}

# =============================================================================
# Outputs
# Security Issue 13: Sensitive values exposed as plain outputs
# Sonar: S6258
# Fix: Add sensitive = true to each output
# =============================================================================
output "db_endpoint" {
  value = aws_db_instance.app_db.endpoint
}

output "db_password_output" {
  value = var.db_password   # password printed to Terraform output
  # sensitive = true        # intentionally commented out
}

output "api_key_output" {
  value = local.app_api_key  # API key exposed in output
  # sensitive = true
}

