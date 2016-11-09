# Contributing to Video on Demand Web module

## Resources for Getting Started

* [Git Tutorial](http://www.tutorialspoint.com/git/) 
* [Angular Style Guide](https://github.com/johnpapa/angular-styleguide)

### Reporting bugs
Open and issue and label it 'bug'.  
Also provide details how to reproduce the bug.

## Requesting New Features
Open an issue and label it 'new feature'

## Contributing (Step-by-step)

1. After finding work to do;

2. [Fork the repo](http://help.github.com/fork-a-repo) on which you're working, clone your forked repo to your local computer, and set up the upstream remote:

        git clone https://github.com/achabill/video_on_demand.git
        git remote add upstream https://github.com/achabill/video_on_demand.git

3. Checkout out a new local branch based on your master and update it to the latest. The convention is to name the branch after the work being done. e.g `adminwebapp` when working on the admin web app, or `clientwebapp`

        git checkout -b adminwebapp master
        git clean -df
        git pull --rebase upstream master

 > Please keep your code clean. Name your branch appropriately. If you find another bug, you want to fix while being in a new branch, please fix it in a separated branch instead.


4. Push the branch to your fork. Treat it as a backup.

        git push -u origin adminwebapp

5. Code
 * Follow the Javascript coding conventions, please read http://javascript.crockford.com/code.html. Take the existing code in the module as a guide as well!
 * Additionally, you can run a style formatter for via your IDE.
 * Follow the formating style here.
  > However, please note that **pull requests consisting entirely of style changes are not welcome on this project**. Style changes in the context of pull requests that also refactor code, fix bugs, improve functionality *are* welcome.

7. Commit

  For every commit please write a short (max 72 characters) summary in the first line followed with a blank line and then more detailed descriptions of the change. Use markdown syntax for simple styling. Please include any issue numbers in your summary.
  
        git commit -m "adminwebapp: Added movie upload"

  **NEVER leave the commit message blank!** Provide a detailed, clear, and complete description of your commit!

8. Issue a Pull Request

  Before submitting a pull request, update your branch to the latest code.
  
        git pull --rebase upstream master

  If you have made many commits, we ask you to squash them into atomic units of work. Most of tickets should have one commit only, especially bug fixes, which makes them easier to back port.

        git checkout master
        git pull --rebase upstream master
        git checkout adminwebapp
        git rebase -i master

  Make sure all unit tests still pass:

        mvn clean package

  Push changes to your fork:

        git push -f

  In order to make a pull request,
  * Navigate to the modules repository you just pushed to (e.g. https://github.com/your-user-name/video_on_demand)
  * Click "Pull Request".
  * Write your branch name in the branch field (this is filled with "master" by default)
  * Click "Update Commit Range".
  * Ensure the changesets you introduced are included in the "Commits" tab.
  * Ensure that the "Files Changed" incorporate all of your changes.
  * Fill in some details about your potential patch including a meaningful title.
  * Click "Send pull request".
  
  Thanks for that -- we'll get to your pull request ASAP. We love pull requests!

### Additional Resources
* [General GitHub documentation](http://help.github.com/)
* [GitHub pull request documentation](http://help.github.com/send-pull-requests/)