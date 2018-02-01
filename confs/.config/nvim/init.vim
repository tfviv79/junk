" vim: set foldmethod=marker:
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
set list

"##### key map ########"
noremap <C-p> :bp<CR>
noremap <C-n> :bn<CR>
noremap <C-j> :cn<CR>
noremap <C-k> :cp<CR>
"" insert timestamp
inoremap <C-b><C-b> <C-R>=strftime("start: [%Y-%m-%d %H:%M:%S]")<CR>
inoremap <C-b><C-e> <C-R>=strftime("end  : [%Y-%m-%d %H:%M:%S]")<CR>
inoremap <C-b><C-t> <C-R>=strftime("[%Y-%m-%d %H:%M:%S]")<CR>
inoremap <C-b><C-d> <C-R>=strftime("[%Y-%m-%d]")<CR>
inoremap <C-b><C-t><C-n> <C-R>=strftime("##NOTE## [%Y-%m-%d %H:%M:%S]")<CR>

inoremap <C-b><C-u> <Esc>k:r!uuidgen<CR>A


let $CACHE = empty($XDG_CACHE_HOME) ? expand('$HOME/.cache') : $XDG_CACHE_HOME
let $CONFIG = empty($XDG_CONFIG_HOME) ? expand('$HOME/.config') : $XDG_CONFIG_HOME
let $DATA = empty($XDG_DATA_HOME) ? expand('$HOME/.local/share') : $XDG_DATA_HOME


set ttimeoutlen=10

" {{{ dein
let s:dein_dir = expand('$DATA/dein')

if &runtimepath !~# '/dein.vim'
    let s:dein_repo_dir = s:dein_dir . '/repos/github.com/Shougo/dein.vim'

    " Auto Download
    if !isdirectory(s:dein_repo_dir)
        call system('git clone https://github.com/Shougo/dein.vim ' . shellescape(s:dein_repo_dir))
    endif

    execute 'set runtimepath^=' . s:dein_repo_dir
endif


" dein.vim settings

if dein#load_state(s:dein_dir)
    call dein#begin(s:dein_dir)

    let s:toml_dir = expand('$CONFIG/dein')

    call dein#load_toml(s:toml_dir . '/plugins.toml', {'lazy': 0})
    call dein#load_toml(s:toml_dir . '/lazy.toml', {'lazy': 1})
    if has('python3')
        call dein#load_toml(s:toml_dir . '/python.toml', {'lazy': 1, 'on_ft': 'python'})
    endif
    call dein#load_toml(s:toml_dir . '/rust.toml', {'lazy': 1, 'on_ft': 'rust'})

    call dein#end()
    call dein#save_state()
endif

if has('vim_starting') && dein#check_install()
    call dein#install()
endif
" }}}

" {{{ deoplete-rust
" https://github.com/sebastianmarkow/deoplete-rust
" Set fully qualified path to racer binary. If it is in your PATH already use which racer. (required)
let g:deoplete#sources#rust#racer_binary='$HOME/.cargo/bin/racer'

" Set Rust source code path (when cloning from Github usually ending on /src). (required)
let g:deoplete#sources#rust#rust_source_path='$HOME/.multirust/toolchains/stable-x86_64-unknown-linux-gnu/lib/rustlib/src/rust/src/'


"Show duplicate matches.
"let g:deoplete#sources#rust#show_duplicates=1

"To disable default key mappings (gd & K) add the following
"let g:deoplete#sources#rust#disable_keymap=1

"Set max height of documentation split.
"let g:deoplete#sources#rust#documentation_max_height=20
" }}}
