#!/bin/bash
#SERVER STUFF 
#Update first
sudo apt-get -y update
sudo apt-get -y upgrade
#Upgrade system as an option
#Install postgres
sudo apt-get -y install postgresql tomcat7-user libtcnative-1 makepasswd

#KERNEL STUFF
#Make some changes to the kernel params
#TODO This needs to be based on free memory.
sudo sh -c "echo '
kernel.shmmax = 1073741824
net.core.rmem_max = 8388608
net.core.wmem_max = 8388608' >> /etc/sysctl.conf" 
sudo sysctl -p

#POSTGRES STUFF
#Backup the Postgres configuration file
sudo cp /etc/postgresql/9.1/main/postgresql.conf /etc/postgresql/9.1/main/postgresql.conf.bak

#TODO This needs to be based on memory allocated to Postgres
sudo sh -c "sudo -u postgres echo -e \"data_directory = '/var/lib/postgresql/9.1/main'\n         
hba_file = '/etc/postgresql/9.1/main/pg_hba.conf'\n       
ident_file = '/etc/postgresql/9.1/main/pg_ident.conf'\n  
external_pid_file = '/var/run/postgresql/9.1-main.pid'\n         
port = 5432\n                            
max_connections = 100\n                  
unix_socket_directory = '/var/run/postgresql'\n         
ssl = true\n                             
shared_buffers = 512MB\n                  
log_line_prefix = '%t '\n                 
datestyle = 'iso, mdy'\n
lc_messages = 'en_US.UTF-8'\n                     
lc_monetary = 'en_US.UTF-8'\n                     
lc_numeric = 'en_US.UTF-8'\n                     
lc_time = 'en_US.UTF-8'\n                        
default_text_search_config = 'pg_catalog.english'\n
effective_cache_size = 3500MB\n
checkpoint_segments = 32\n
checkpoint_completion_target = 0.8\n
wal_buffers = 4MB\n
synchronous_commit = off\n
wal_writer_delay = 10000ms\n\"  > /etc/postgresql/9.1/main/postgresql.conf"

#Restart postgres
sudo /etc/init.d/postgresql restart
sudo ufw status 
echo "Please remember to configure your firewall!"