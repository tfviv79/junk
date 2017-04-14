#!/usr/bin/env python
## encoding: utf-8

import unicodedata

def char_display_width(c, encoding="utf-8", unicode_char_type=["W", "F", "A"]):
	""" obtain charcter code width on display
	>>> char_display_width('c')
	1
	>>> char_display_width('あ')
	2
	>>> char_display_width(u'c'[0])
	1
	>>> char_display_width(u'\u3042')
	2
	"""
	if type(c) != unicode:
		c = unicode(c, encoding)
	
	if unicodedata.east_asian_width(c) in unicode_char_type:
		return 2
	else:
		return 1

def string_display_width(s, encoding="utf-8", unicode_char_type=["W", "F", "A"]):
	""" obtain string width on display
	>>> string_display_width('cあ')
	3
	"""
	width = 0
	for c in unicode(s, encoding):
		width += char_display_width(c, encoding, unicode_char_type)
	return width

def string_shorten(s, width, padding_str=" ", padding_dict="right"
		, encoding="utf-8", shorten_string="...", , unicode_char_type=["W", "F", "A"]):
	""" shorten string for display
	>>> string_shorten("1234567890", 10)
	'1234567890'
	>>> string_shorten("1234567890", 9)
	'123456...'
	>>> string_shorten("12345", 9, padding_str=" ")
	'12345    '
	"""
	ret = []
	shorten_size = 0
	remain = []
	size = 0
     	sh_len = string_display_width(shorten_string, encoding, unicode_char_type)
	for c in s:
		c_size = char_display_width(c, encoding, **argv)
		if c_size + size + sh_len <= width:
			ret.append(c)
			shorten_size = size + c_size
		elif c_size + size <= width:
			remain.append(c)

		size += c_size
		if size > width:
			break
	if size <= width:
		ret_str = "".join(ret + remain)
	else:
		ret_str = "".join(ret) + shorten_string
		size = shorten_size + sh_len
	if padding_str is not None:
		if width - size > 0:
			padding = " "*(width-size)
			if padding_dict == "right":
				return ret_str + padding
			else:
				return padding + ret_str 

	return ret_str
	
