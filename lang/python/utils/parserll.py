#!/usr/bin/env python
## encoding: utf-8
import collections
Result = collections.namedtuple("Result", ["flg", "ok", "err"])
Result_ok = lambda x: Result(True, x, None)
Result_err = lambda x, *args: Result(False, None, x%args)

def is_iter(l):
    return isinstance(l, collections.Iterable) and not isinstance(l, str)

def flatten(l, nested=-1):
    ret = []
    for e in l:
        if nested != 0 and is_iter(e):
            ret.extend(flatten(e, nested - 1))
        else:
            ret.append(e)
    return ret

class InputSource(object):
    def __init__(self, s, pos=0):
        self.s = s
        self.pos = pos
    def read(self):
        if self.pos >= len(self.s):
            return Result_err("EOF")
        return Result_ok(self.s[self.pos])
    def next(self):
        return InputSource(self.s, self.pos + 1)

def _checker_is_digit(ch):
    return '0' <= ch <= '9'

def _checker_is_alpha_lower(ch):
    return 'a' <= ch <= 'z'

def _checker_is_alpha_upper(ch):
    return 'A' <= ch <= 'Z'

def _checker_is_alpha(ch):
    return _checker_is_alpha_lower(ch) or _checker_is_alpha_upper(ch)

def _checker_is_alphanum(ch):
    return _checker_is_alpha(ch) or _checker_is_digit(ch)

def _checker_is_space(ch):
    return ch == ' ' or ch == '\t'

def _checker_is_char(ch):
    def _checker_is_char_inner(c):
        return ch == c
    return _checker_is_char_inner

def _checker_in(container):
    def _checker_in_inner(c):
        return c in container
    return _checker_in_inner

class Parser(object):
    def __init__(self, proc):
        self.proc = proc
    def parse(self, s):
        rr = self.proc(s)
        s, ret = rr
        return s, ret
    ## combinator functions
    def n(self, p):
        def proc(s):
            s, ret = self.parse(s)
            if not ret.flg:
                return s, ret
            s, ret2 = p.parse(s)
            if not ret2.flg:
                return s, ret2
            return s, Result_ok(ret2.ok)
        return Parser(proc)
    def p(self, p):
        def proc(s):
            s, ret = self.parse(s)
            if not ret.flg:
                return s, ret
            s, ret2 = p.parse(s)
            if not ret2.flg:
                return s, ret2
            return s, Result_ok(ret.ok)
        return Parser(proc)
    def maybe(self, p):
        def proc(s):
            acc = []
            s, ret = self.parse(s)
            if not ret.flg:
                return s, ret
            acc.append(ret.ok)

            copy_s = s
            s, ret = p.parse(s)
            if not ret.flg:
                s = copy_s
            else:
                acc.append(ret.ok)
            return s, Result_ok(acc)
        return Parser(proc)
    def many(self, p):
        def proc(s):
            acc = []
            s, ret = self.parse(s)
            if not ret.flg:
                return s, ret
            acc.append(ret.ok)
            while True:
                copy_s = s
                s, ret = p.parse(s)
                if not ret.flg:
                    s = copy_s
                    break
                acc.append(ret.ok)
            return s, Result_ok(acc)
        return Parser(proc)
    def many1(self, p):
        return self.c(p).many(p)
    def c(self, p):
        def parse_continue(s):
            ret_a = []
            s, ret = self.proc(s)
            if not ret.flg:
                return s, ret
            ret_a.append(ret.ok)
            s, ret = p.proc(s)
            if not ret.flg:
                return s, ret
            ret_a.append(ret.ok)
            return s, Result_ok(ret_a)
        return Parser(parse_continue)
    def o(self, p):
        def parse_selection(s):
            copy_s = s
            ret = self.proc(s)
            if ret.flg:
                return s, ret
            s = copy_s
            s, ret = p.proc(s)
            if ret.flg:
                return s, ret
            return s, Result_err("not matched")
        return Parser(parse_selection)
    ## transrator 
    def trans(self, proc):
        def trans_func(s):
            s, ret = self.proc(s)
            if not ret.flg:
                return s, ret
            new_result = proc(ret.ok)
            return s, Result_ok(new_result)
        return Parser(trans_func)
    def to_s(self):
        def trans_to_s(ok):
            return "".join([str(r) for r in ok])
        return self.trans(trans_to_s)
    def flat(self, nested=-1):
        def trans_flat(ok):
            return flatten(ok, nested)
        return self.trans(trans_flat)

##base parser parts
def checkparser(checker, name=None):
    if name is None:
        name = checker.__name__
    def check(s):
        c = s.read()
        if not c.flg:
            return s, c
        if checker(c.ok):
            ns = s.next()
            return ns, Result_ok(c.ok)
        return s, Result_err("%s is not %s", c.ok, name)
    return Parser(check)

def wrapper(parser):
    def wrapped_parser(s):
        print("#### wraped %s"%(s.read(),))
        return parser().parse(s)
    return Parser(wrapped_parser)

def many(p):
    def proc(s):
        acc = []
        while True:
            copy_s = s
            s, ret = p.parse(s)
            if not ret.flg:
                s = copy_s
                break
            acc.append(ret.ok)
        return s, Result_ok(acc)
    return Parser(proc)

def many1(p):
    return p.many(p)

def o(*parsers):
    def proc(s):
        for parser in parsers:
            copy_s = s
            s, ret = parser.parse(s)
            if ret.flg:
                return s, ret
            s = copy_s
        return copy_s, Result_err("not matched") 
    return Parser(proc)


def c(*parsers):
    def proc(s):
        acc = []
        for parser in parsers:
            s, ret = parser.parse(s)
            if not ret.flg:
                return s, ret
            acc.append(ret.ok)
        return s, Result_ok(acc)
    return Parser(proc)

def maybe(p):
    def proc(s):
        copy_s = s
        s, ret = p.parse(s)
        if not ret.flg:
            s = copy_s
            return s, Result_ok("")
        return s, ret
    return Parser(proc)

##base parser
def char1(ch):
    return checkparser(lambda x: x == ch, ch)
 
one_digit    = checkparser(_checker_is_digit, "digit")
one_alpha    = checkparser(_checker_is_alpha, "alpha")
one_alphanum = checkparser(_checker_is_alphanum, "alphanum")
space        = checkparser(_checker_is_space, "space")
spaces       = many(space)
digit     = many1(one_digit).to_s()
sign      = o(char1("-"), char1("+"))
intp      = maybe(sign).c(digit).to_s()
floatp    = intp.maybe(char1(".").c(intp)).to_s()
identity  = o(char1("_"), one_alpha).many(o(char1("_"), one_alphanum))


##helper function
def parse(parser, s):
    return parser.parse(InputSource(s))[1]

if __name__ == "__main__":
    def testparse(parser, s):
        print("INPUT:", s)
        ret = parse(parser, s)
        print("RESULT:", ret)
    import unittest

    def _exp():
        global exp
        print("###exp", exp)
        return exp
    mul = o(char1("*"), char1("/"))
    term = o(intp, wrapper(_exp))
    add_op = term.many(spaces.n(mul).p(spaces).c(term))
    exp = add_op.many(spaces.n(sign).p(spaces).c(add_op))

    testparse(exp, "-1234567890 + -3")
    import sys
    if len(sys.argv) > 1:
        testparse(exp, sys.argv[1])
