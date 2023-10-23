## Notes on setting up Metals for Vim
1. Create the file `.jvmops`
2. Paste `Xss4m` or `-Xss4m` into `jvmops`
    - Xss8m
    - Xms1G
    - Xmx8G
3. Copy the file to both `$HOME/.bloop/` and `./.bloop`
4. Find the process ID of the bloop process by running the following command:
    - `ps aux | grep bloop`
    - Kill the bloop process by running the following command, replacing `process_id` with the process ID you found in the previous step:
    - `kill <process_id>`
5. Open your project and check if the error message `Compilation get stuck on 10%` is present. For more information, see the following GitHub issues:
    - https://github.com/scalameta/metals/issues/1347
    - https://github.com/scalacenter/bloop/issues/1168
6. To fix the message "downloading scalafix", use a proxy. You can do this by exiting Vim and then opening it again.

## Misc
- The default leader key in Neovim is backslash (\), while in Lunarvim it is the space key.
- One of my previous problems was caused by uncommenting the following line in my vim config file and setting its value to `true`, `false`, or `"on"`:
    - `metals_config.init_options.statusBarProvider = "on"`
    - If you do this, you will not see any messages from Metals and will not be notified of what is happening.
- For more information on using Metals with a remote language server, see the Metals documentation:
    - https://scalameta.org/metals/docs/integrations/remote-language-server/
