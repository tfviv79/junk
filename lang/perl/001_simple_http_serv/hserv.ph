use strict;
use warnings;

use HTTP::Daemon;
use HTTP::Status;
use File::Spec;


my $d = HTTP::Daemon->new(
    LocalAddr => "127.0.0.1",
    LocalPort => shift || 9090,
) || die $!;


print "Start up ", $d->url , "\n";

while (my $c = $d->accept) {
    while (my $r = $c->get_request) {
        if ($r->method eq 'GET') {
            my $path = File::Spec->rel2abs($r->url->path);
            if ($path eq '/') {
                $path = "/index.html";
            }
            my $file_path = "./resources" . $path;
            print STDERR "GET ", $r->url->path, "\n";
            $c->send_file_response($file_path);
        } else {
            $c->send_error(RC_FORBIDDEN);
        }
    }
    $c->close;
    undef($c);
}
