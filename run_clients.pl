#!/usr/bin/perl -w

use strict;
use threads;

my $threads_number = 400;
my $cmd = './client localhost:1234';
my @threads;

sub runThreads{
	system($cmd);
	threads->exit();
}

for(my $i = 0; $i < $threads_number; $i++){
	push(@threads, threads->new(\&runThreads, $_));
}

foreach(@threads){
	$_->join();
}


print("\nAll threads completed!\n")

