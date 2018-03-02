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
import collections


class NonBlockingInput(object):
    def __init__(self, fpin, raw_mode=None):
        self.fpin = fpin
        self.raw_mode = raw_mode
        self.isatty = os.isatty(fpin.fileno())
        if self.isatty:
            self.org_term_flag = termios.tcgetattr(fpin.fileno())
        self.org_io_flag = fcntl.fcntl(fpin.fileno(), fcntl.F_GETFL)
    def recv(self, timeout_sec=1, size=4096, trynum=10):
        """
            @return tuple (code, val)
                code:
                    0: no val (val is None)
                    1: all data get (at least over size)
                    2: partial get
        """
        ## convert from sec to milli sec
        poll_event = self.pollobj.poll(timeout_sec * 1000.0)
        print("#### wait poll {}".format(poll_event))
        if poll_event:
            print("#### wait poll val {} -> {}".format(poll_event[0][1], poll_event[0][1] & select.POLLIN != 0))
            if poll_event[0][1] & (select.POLLIN|select.POLLHUP) != 0:
                buf = []
                totalsize = 0
                while trynum >= 0:
                    trynum -= 1
                    d = self.fpin.read(size)
                    print("#### read {}".format(d))
                    if d:
                        buf.append(d)
                        totalsize += len(d)
                    else:
                        if len(buf) == 0:
                            break
                        return (2, "".join(buf))
                    if totalsize >= size:
                        return (1, "".join(buf))
                if totalsize != 0:
                    return (2, "".join(buf))
            if poll_event[0][1] & select.POLLHUP != 0:
                return (9, None)
        return (0, None)
    def __enter__(self):
        if self.raw_mode == "cbreak":
            if self.isatty:
                tty.setcbreak(self.fpin.fileno())
        elif self.raw_mode == "raw":
            if self.isatty:
                tty.setraw(self.fpin.fileno())
        fcntl.fcntl(self.fpin.fileno(), fcntl.F_SETFL, self.org_io_flag | os.O_NONBLOCK)
        self.pollobj = select.poll()
        self.pollobj.register(self.fpin.fileno(), select.POLLIN | select.POLLHUP | select.POLLERR)
        return self
    def __exit__(self, exc_type, exc_value, traceback):
        try:
            self.pollobj.unregister(self.fpin.fileno())
        finally:
            try:
                fcntl.fcntl(self.fpin.fileno(), fcntl.F_SETFL, self.org_io_flag)
            finally:
                if self.isatty:
                    termios.tcsetattr(self.fpin.fileno(), termios.TCSADRAIN, self.org_term_flag)
        return False

class KeyHandler(object):
    def __init__(self):
        pass
    def proc(self, inputs):
        if inputs is None or len(inputs) == 0:
            return ("in", "")
        if inputs[0] == '\x11': # C-q
            return ("stop", None)
        else:
            return ("in", inputs)

def main(fpin, fpout, argv):
    nonFpin = NonBlockingInput(fpin, raw_mode="cbreak")
    hander = KeyHandler()
    with nonFpin as nonIn:
        buf = []
        while True:
            ch = nonIn.recv(timeout_sec=1, size=1)
            if ch[0] in [1,2]:
                hp = hander.proc(ch[1])
                if hp[0] == "stop":
                    break
                else:
                    buf.append(ch[1])
            elif ch[0] == 9:
                break
        fpout.write("Wait input : %s\n"%buf)

if __name__ == "__main__":
    main(sys.stdin, sys.stdout, sys.argv)
