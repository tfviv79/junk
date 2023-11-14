#!/usr/bin/env python3
import sys
import dataclasses

@dataclasses.dataclass
class Input:
    input: str
    pos: int
    def __init__(self, input, pos=0):
        self.input = input
        self.pos = pos
    def save(self):
        return Input(self.input, self.pos)
    def restore(self, pos):
        ret = Input(pos.input, pos.pos)
        return ret
    def get(self):
        if self.pos < len(self.input):
            return self.input[self.pos]
        else:
            return None
    def getN(self, n):
        if self.pos + n < len(self.input):
            return self.input[self.pos:self.pos + n]
        else:
            return None
    def next(self):
        v = self.get()
        if v:
            self.pos += 1
        return v
    def nextN(self, n):
        strval = self.getN(n)
        if strval:
            self.pos += n
            return strval
        else:
            return None

    def iter(self):
        while True:
            v = self.next()
            if v:
                yield v
            else:
                break

@dataclasses.dataclass
class Token:
    n: str
    v: str
    pos: Input

    def __init__(self, n, v, pos):
        self.n = n
        self.v = v
        self.pos = pos

def parser(input):
    pos = input.save()
    v = input.next()
    return Token("any", v, pos)

def ch(chval, name=""):
    def in_parser(input):
        v = input.get()
        if v == chval:
            pos = input.save()
            input.next()
            return Token(name, v, pos)
        else:
            return None
    return in_parser

def chs(strval, name=""):
    lenN = len(strval)
    def in_parser(input):
        pos = input.save()
        v = input.nextN(lenN)
        if v:
            return Token(name, v, pos)
        else:
            input.restore(pos)
            return None
    return in_parser

CHARS_SP = [' ', '\t', '\n']
CHARS_NUM = [chr(ch) for ch in range(ord('0'), ord('9'))]
CHARS_ALPHA = [chr(ch) for ch in range(ord('a'), ord('z'))]
CHARS_ALPHA_C = [chr(ch) for ch in range(ord('A'), ord('Z'))]
CHARS_ID = CHARS_ALPHA + CHARS_ALPHA_C + CHARS_NUM

def _isIdChar(idx, v):
    if v in CHARS_ID:
        if idx == 0 and v in CHARS_NUM:
            return False
        return True
    else:
        return False
def num(input):
    pos = input.save()
    vals = []
    while True:
        v = input.get()
        if v in CHARS_NUM:
            vals.append(v)
            input.next()
        else:
            if len(vals) == 0:
                return None
            else:
                break
    return Token("num", "".join(vals), pos)

def id(input):
    pos = input.save()
    vals = []
    while True:
        v = input.get()
        if _isIdChar(len(vals), v):
            vals.append(v)
            input.next()
        else:
            if len(vals) == 0:
                return None
            else:
                break
    return Token("id", "".join(vals), pos)

def spaces(input):
    while True:
        v = input.get()
        if v in CHARS_SP:
            input.next()
            continue
        else:
            break
    return None


def main(argv):
    inp = Input("let a = 1* (2+3);")
    print("v:", chs("let")(inp))
    print("v:", spaces(inp))
    print("v:", id(inp))
    print("v:", spaces(inp))
    print("v:", chs("=")(inp))
    print("v:", spaces(inp))
    print("v:", num(inp))
    print("v:", spaces(inp))
    print("v:", ch("*")(inp))
    print("v:", spaces(inp))
    print("v:", ch("(")(inp))
    print("v:", spaces(inp))
    print("v:", num(inp))
    print("v:", spaces(inp))
    print("v:", ch("+")(inp))
    print("v:", spaces(inp))
    print("v:", num(inp))
    print("v:", spaces(inp))
    print("v:", ch(")")(inp))
    print("v:", spaces(inp))
    print("v:", ch(";")(inp))

if __name__ == "__main__":
    main(sys.argv)
