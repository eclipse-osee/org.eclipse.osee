#!/usr/local/bin/perl
use strict;
use warnings;
use English;
use Net::Telnet;

die 
"Usage: [shutdown|isAlive] address port\n"

unless scalar(@ARGV);

my $cmd = shift @ARGV;
my $ADDRESS = shift @ARGV;
my $PORT = shift @ARGV;

if ( $cmd eq 'shutdown'){
  my $telnet = new Net::Telnet (Timeout => 10, Errmode => 'die'); 
  $telnet->open(Host=>$ADDRESS, Port=>$PORT) or die "telnet open failed\n"; 
  $telnet->print('');
  $telnet->print('osee_shutdown -oseeOnly'); 
  $telnet->waitfor('/Osee Shutdown Complete$/i'); 
  $telnet->print('disconnect'); 
  $telnet->waitfor('/Disconnect from console?.*$/i'); 
  $telnet->print('y'); 
  $telnet->close();
  print "OK"
} elsif ($cmd eq 'isAlive') {
  my $telnet = new Net::Telnet (Timeout => 10, Errmode => 'die'); 
  $telnet->open(Host=>$ADDRESS, Port=>$PORT) or die "telnet open failed\n"; 
  $telnet->print('disconnect'); 
  $telnet->waitfor('/Disconnect from console?.*$/i'); 
  $telnet->print('y'); 
  $telnet->close();
  print "Alive"
}

