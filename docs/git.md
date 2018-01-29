GIT Guidelines
==============

Branches
--------

The GIT Repository has two main branches:

- `master`: Contains the current release state. Every tagged release is made on
  this branch. Merges will usually only be made directly from develop.
- `develop`: Contains the current development state. Every feature will be
  merged into this branch first before getting released.

Additionally, every new feature/change will get their own so-called "feature
branch". These have the format `{type}/{issueid}-{description}`, with type
being one of `feature`, `bugfix` or `task` (From the redmine issue). Examples
are:

- `feature/12050-login-system`
- `bugfix/12204-error-handling-fix`
- `task/23202-add-component`

They should be branched off from the `develop` branch, and when completed,
merged into `develop`.

This common model is inspired by
[this article](http://nvie.com/posts/a-successful-git-branching-model/).


Commit Messages
---------------

Here are our guidelines for commit messages:

- Limit the subject line to 50 characters. (Exceptions is meta info like
  connected issue numbers)
- Capitalize the subject line.
- Do not end the subject line with a period.
- Use the imperative mood in the subject line. (`Change something` instead of
  `Changed something`)
- If the changes needs further explanation, add a body section separated with a
  blank line after the subject.
- Wrap the body at 72 characters.

See [this article](https://chris.beams.io/posts/git-commit/) for more info about
how commit messages should be written.
