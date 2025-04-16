" vim: set foldmethod=marker:

filetype off
filetype plugin indent off


" reset augroup
augroup MyAutoCmd
    autocmd!
augroup END

set expandtab
set smarttab
set smartcase
set tabstop=4
set shiftwidth=4
set softtabstop=4
"set nowrap
set number
set relativenumber
set list
set listchars=tab:>-,trail:-,eol:$,nbsp:%
set nofixeol
set title
set foldmethod=indent
set foldlevel=9999

set encoding=utf-8
set fileencodings=iso-2022-jp,euc-jp,cp932,utf-8
set fileformats=unix,dos,mac

au FileType make setlocal noexpandtab nosmarttab
au FileType vue setlocal tabstop=2 shiftwidth=2 softtabstop=2
au FileType typescript setlocal tabstop=2 shiftwidth=2 softtabstop=2
au FileType javascript setlocal tabstop=2 shiftwidth=2 softtabstop=2
au FileType yaml setlocal tabstop=2 shiftwidth=2 softtabstop=2

syntax enable
set background=dark

"## yank to os clipboard
set clipboard=unnamed

"## complition option
set wildmenu wildmode=list:longest

" join-line behavior(improvement behavior when use J, remove charctor >, etc...)
set formatoptions-=j

"##### key map ########"
" mapleader (default \)
let mapleader = "\<Space>"
"""noremap <Leader>a :echo "hello"<CR>

"" change buffer
noremap <C-p> :bp<CR>
noremap <C-n> :bn<CR>
noremap <C-j> :cn<CR>
noremap <C-k> :cp<CR>
"" insert timestamp
inoremap <C-l><C-b> <C-R>=strftime("start: [%Y-%m-%d %H:%M:%S]")<CR>
inoremap <C-l><C-e> <C-R>=strftime("end  : [%Y-%m-%d %H:%M:%S]")<CR>
inoremap <C-l><C-t> <C-R>=strftime("[%Y-%m-%d %H:%M:%S]")<CR>
inoremap <C-l><C-d> <C-R>=strftime("[%Y-%m-%d]")<CR>
inoremap <C-l><C-n> <C-R>=strftime("##NOTE## [%Y-%m-%d %H:%M:%S]")<CR>
inoremap <C-l><C-j> <C-R>=strftime("[%Y-%m-%d %H:%M:%S]{{{\n\n}}}\n")<CR><Up><Up>
"" inoremap <C-l><C-u> <Esc>k:r!uuidgen<CR>A
"" insert filename path
inoremap <C-l><C-f> <C-R>=expand("%:p")<CR>
"" open current file directory
noremap <C-l><C-o> :Explore %:h<CR>
noremap <C-l><C-l> :lcd %:h<CR>
noremap <C-l><C-f> :let @* = expand("%") . ':' . line('.') . "\n"<CR>
noremap <C-l><C-g> :let @* = expand("%:p") . "\n"<CR>


noremap <Leader>q :n ~/Documents/TODO.txt<CR>
noremap <Leader>w :n ~/Documents/memo.txt<CR>
noremap <Leader>b :terminal git blame %<CR>
noremap <Leader>c :<C-u>setlocal cursorline! cursorcolumn!<CR>

vnoremap <Leader>j :s/<Space>/<TAB>/g<CR>
vnoremap <Leader>k :s/<Space>\+\|<Space>\+/<TAB>/g<CR>

cnoremap <C-a> <Home>
cnoremap <C-e> <End>
cnoremap <C-f> <Right>
cnoremap <C-b> <Left>
