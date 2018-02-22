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


@contextlib.contextmanager
def toRaw(fpin):
    org_flg = termios.tcgetattr(fpin.fileno())
    try:
        tty.setcbreak(fpin.fileno())
        old_io_flag = fcntl.fcntl(fpin.fileno(), fcntl.F_GETFL)
        try:
            fcntl.fcntl(fpin.fileno(), fcntl.F_SETFL, old_io_flag | os.O_NONBLOCK)
            try:
                p = select.poll()
                p.register(fpin.fileno(), select.POLLIN)
                yield (p, fpin)
            finally:
                p.unregister(fpin.fileno())
        finally:
            fcntl.fcntl(fpin.fileno(), fcntl.F_SETFL, old_io_flag)
    finally:
        termios.tcsetattr(fpin.fileno(), termios.TCSADRAIN, org_flg)




def main(fpin, fpout, argv):
    with toRaw(fpin) as val:
        p, fpin = val
        while True:
            evnt = p.poll(5000)
            if evnt:
                ch = fpin.read(1)
                fpout.write("Input: [%s] with event %s\n"%(ch, evnt))
                if ch == 'q':
                    break
            else:
                fpout.write("Wait input \n")

if __name__ == "__main__":
    main(sys.stdin, sys.stdout, sys.argv)
