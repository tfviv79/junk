#!/usr/bin/env python
"""
non-block input 
"""
import sys
import termios
import fcntl
import tty
import os
import select
import contextlib
import subprocess


@contextlib.contextmanager
def toRaw(fpin):
    old_io_flag = fcntl.fcntl(fpin.fileno(), fcntl.F_GETFL)
    try:
        fcntl.fcntl(fpin.fileno(), fcntl.F_SETFL, old_io_flag | os.O_NONBLOCK)
        try:
            p = select.poll()
            p.register(fpin.fileno(), select.POLLIN | select.POLLHUP)
            yield (p, fpin)
        finally:
            p.unregister(fpin.fileno())
    finally:
        fcntl.fcntl(fpin.fileno(), fcntl.F_SETFL, old_io_flag)


def main(fpin, fpout, argv):
    proc = subprocess.Popen(["/bin/bash"], stdin=subprocess.PIPE, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
    cmd = b"""
    for i in `seq 1 5`; do
       sleep 5;
       echo "output $i";
    done\n
    """
    proc.stdin.write(cmd)
    proc.stdin.close()
    with toRaw(proc.stdout) as val:
        p, fpin = val
        while True:
            evnt = p.poll(8000)
            if evnt:
                if evnt[0][1] & select.POLLIN != 0:
                    ch = fpin.read(20)
                    fpout.write("Input: [%s] with event %s\n"%(ch, evnt))
                else:
                    fpout.write("error event %s\n"%(evnt))
                    break
            else:
                fpout.write("Wait input \n")

if __name__ == "__main__":
    main(sys.stdin, sys.stdout, sys.argv)
