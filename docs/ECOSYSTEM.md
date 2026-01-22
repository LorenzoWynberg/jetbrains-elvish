# Elvish Ecosystem

Useful tools and resources from the Elvish community that complement this plugin.

> Source: [awesome-elvish](https://github.com/elves/awesome-elvish)

## Completions

| Tool | Description |
|------|-------------|
| [carapace-bin](https://github.com/rsteube/carapace-bin) | Completions for 400+ commands. Highly recommended. |
| [elvish-completions](https://github.com/zzamboni/elvish-completions) | Completions for cd, git, vcsh |
| [elvish-bash-completion](https://github.com/aca/elvish-bash-completion) | Convert any bash completion to Elvish |

## Prompt Themes

| Tool | Description |
|------|-------------|
| [Starship](https://starship.rs) | Cross-shell, minimal, fast prompt (Rust) |
| [oh-my-posh](https://ohmyposh.dev) | Cross-shell, highly configurable prompt (Go) |
| [chain](https://github.com/zzamboni/elvish-themes) | Configurable prompt with Git support |
| [powerline](https://github.com/muesli/elvish-libs) | Powerline style prompt |

## Directory Navigation

| Tool | Description |
|------|-------------|
| [zoxide](https://github.com/ajeetdsouza/zoxide) | Smart cd that learns your habits |
| [direlv](https://github.com/tesujimath/direlv) | Directory-specific environments (like direnv) |
| [dir module](https://github.com/zzamboni/elvish-modules) | Directory stack, `cd -` support |

## Development Tools

| Tool | Description |
|------|-------------|
| [python/virtualenv](https://github.com/tesujimath/bash-env-elvish) | Python virtualenv support |
| [nvm](https://github.com/tesujimath/bash-env-elvish) | Node Version Manager support |
| [mamba](https://github.com/iandol/elvish-modules) | Conda/mamba/micromamba support |
| [nix](https://github.com/zzamboni/elvish-modules) | Nix package manager utilities |

## Utility Modules

| Module | Description |
|--------|-------------|
| [bang-bang](https://github.com/zzamboni/elvish-modules) | `!!` and `!$` keybindings |
| [alias](https://github.com/zzamboni/elvish-modules) | Persistent aliases, bash-style parsing |
| [long-running-notifications](https://github.com/zzamboni/elvish-modules) | Notify when long commands finish |
| [spinners](https://github.com/zzamboni/elvish-modules) | Progress spinners for scripts |

## Testing

| Tool | Description |
|------|-------------|
| [elvish-tap](https://github.com/tesujimath/elvish-tap) | Test Anything Protocol (TAP) for Elvish |
| [velvet](https://github.com/giancosta86/velvet) | Functional testing framework |

## Configuration Examples

| Resource | Description |
|----------|-------------|
| [dot_elvish](https://github.com/zzamboni/dot_elvish) | Well-documented rc.elv example |
| [oh-my-elvish](https://github.com/darcy-shen/oh-my-elvish) | User-friendly Elvish configuration framework |

## Installing Packages

Elvish has a built-in package manager (`epm`):

```elvish
use epm
epm:install github.com/zzamboni/elvish-modules
```

Then use modules:
```elvish
use github.com/zzamboni/elvish-modules/alias
```

## See Also

- [Elvish Official Docs](https://elv.sh/ref/)
- [awesome-elvish](https://github.com/elves/awesome-elvish) - Full community resource list
