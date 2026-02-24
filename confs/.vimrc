" vim: set foldmethod=marker:

filetype on 
filetype plugin indent on


" reset augroup
augroup MyAutoCmd
    autocmd!
augroup END

set tabstop=4
set shiftwidth=4
set softtabstop=4
set autoindent
set smartindent
set expandtab
set smartcase
"set nowrap

set number
set relativenumber
set hlsearch
nmap <Esc><Esc> :nohlsearch<CR><Esc>
set noswapfile
set nobackup
set nofixeol
set foldmethod=indent
set foldlevel=9999

"不可視文字(tab とか)の可視化
set list
set listchars=tab:>-,trail:-,nbsp:%,extends:>,precedes:<,eol:$
set title

set encoding=utf-8
set fileencodings=utf-8,iso-2022-jp,cp932,euc-jp
set fileformats=unix,dos,mac
set statusline=%F%m%r%h%w\ [FORMAT=%{&ff}]\ [TYPE=%Y]\ [ASCII=\%03.3b]\ [HEX=\%02.2B]\ [POS=%04l,%04v][%p%%]\ [LEN=%L]

au FileType make setlocal noexpandtab nosmarttab
au FileType vue setlocal tabstop=2 shiftwidth=2 softtabstop=2
au FileType typescript setlocal tabstop=2 shiftwidth=2 softtabstop=2
au FileType javascript setlocal tabstop=2 shiftwidth=2 softtabstop=2
au FileType yaml setlocal tabstop=2 shiftwidth=2 softtabstop=2

syntax on
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
"" open current file directory
noremap <C-l><C-o> :Explore %:h<CR>
noremap <C-l><C-l> :lcd %:h<CR>
noremap <C-l><C-f> :let @* = expand("%") . ':' . line('.') . "\n"<CR>
noremap <C-l><C-g> :let @* = expand("%:p") . ':' . line('.') . "\n"<CR>


noremap <Leader>q :n ~/Documents/TODO.txt<CR>
noremap <Leader>w :n ~/Documents/memo.txt<CR>
noremap <Leader>g :grep -r <cword> ./

noremap <Leader>c :<C-u>setlocal cursorline! cursorcolumn!<CR>
noremap <Leader>b :terminal git blame %<CR>
vnoremap <Leader>j :s/<Space>/<TAB>/g<CR>
vnoremap <Leader>k :s/<Space>\+\|<Space>\+/<TAB>/g<CR>

cnoremap <C-a> <Home>
cnoremap <C-e> <End>
cnoremap <C-f> <Right>
cnoremap <C-b> <Left>
