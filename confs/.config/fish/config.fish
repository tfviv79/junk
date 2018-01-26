set PATH /home/$USER/.cargo/bin $PATH
set PYENV_ROOT /home/$USER/.pyenv/
set PATH $PYENV_ROOT/bin $PATH
set XDG_CONFIG_HOME "$HOME/.config"

set fish_color_command white
set fish_color_autosuggestion green
set fish_color_param cyan

status --is-interactive; and source (pyenv init -|psub)


