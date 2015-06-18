# uacalcsrc

This is the main repository for the source code of the [Universal Algebra
Calculator](http://uacalc.org) (UACalc).

For the GUI version of the program, please visit
[the main UACalc webpage: http://uacalc.org](http://uacalc.org).

## Notes on importing, using, and collaborating on uacalcsrc

The instructions that follow will require entering commands into a terminal
window with some sort of Unix style shell, like bash.

If you are cloning this repository to your local machine, the steps below 
will assume the repository lives in a directory called `~/git/uacalcsrc`, so
this first command creates a `~/git` directory, if it doesn't already exists
(and does nothing if it does exist):

    $ mkdir -p ~/git

### Browsing the source code

If you merely want to browse the UACalc source code, you can do so using the
GitHub webpages, or you can clone the repository to your local drive with a
command like: 

    $ git clone git@github.com:UACalc/uacalcsrc.git ~/git/uacalcsrc

or

    $ git clone https://github.com/UACalc/uacalcsrc.git ~/git/uacalcsrc


### Contributing using fork and pull requests

If you expect to contribute improvements to the source code, instead of cloning
directly it is advisable to first fork the repository to your own GitHub
account, and then clone your own fork.  To do so, login to your GitHub account,
navigate to the [UACalc/uacalcsrc](https://github.com/UACalc/uacalcsrc)
repository, then click the
[Fork link](https://github.com/UACalc/uacalcsrc#fork-destination-box) on the
upper right.  Once the fork is created, clone the forked repository to your
local drive with a command like 

    $ git clone git@github.com:your-user-name/uacalcsrc.git ~/git/uacalcsrc

or

    $ git clone https://github.com/your-user-name/uacalcsrc.git ~/git/uacalcsrc

Now you can modify the source code as you wish and then, if you want to
recommend that your changes be incorporated into the main UACalc/uacalcsrc
repository, you should follow these steps:

1. Commit your changes to your local repository (with an informative commit
   message!).

        $ git commit -m "fixed a bug in the bar method of the Foo class"

2. Push the changes to your remote repository (i.e., to the fork you created above).

		$ git push origin master
		
3. Navigate to the GitHub webpage of your fork and click on the `Pull
   Request` link.  Be sure to include an informative comment justifying the
   recommendation to merge your changes into the main respository.

To keep your fork current with the main UACalc/uacalcsrc repository, see the
section "Updating your fork" below.

### Importing uacalcsrc into Eclipse

There are a number of ways to import this repository into the
[Eclipse IDE](http://www.eclipse.org/). One fairly standard and easy method is
described in this section. 

If you plan to make improvements to the code and expect them to be considered for
adoption in the main UACalc/uacalcsrc repository, please create your own
fork of the repository, as explained in the previous section.

**Steps to import into Eclipse**

1. First, clone the repository to your local drive. If you forked the repo as suggested
   above, then use a command like

        git clone git@github.com:your-user-name/uacalcsrc.git ~/git/uacalcsrc

   or

        git clone https://github.com/your-user-name/uacalcsrc.git ~/git/uacalcsrc

   If you didn't create your own fork, you can clone with the command
   
        git clone https://github.com/UACalc/uacalcsrc.git ~/git/uacalcsrc


2. Launch Eclipse and use the File menu to import the source code:

        File --> Import --> Git --> Projects from Git

   then click Next.

3. Select `Local`, click Next, then click Add and browse to the directory where
   you clone the repository in Step 1 above (e.g., ~/git/uacalcsrc).

4. Select the uacalcsrc repository and click Next.

5. Select the `Import existing project` radio button, click Next, and then
   select the algebra project and click finish.

### Updating your fork

When improvements are made to the "upstream" UACalc/uacalcsrc repository,
you will probably want to update your fork to incorporate these
changes.  Below is a list of the commands that accomplish this, but see the page
[here](https://help.github.com/articles/configuring-a-remote-for-a-fork/) and
[here](https://help.github.com/articles/syncing-a-fork/)
for details.

1. Specify the upstream repository.

        $ git remote add upstream git@github.com:UACalc/uacalcsrc.git

2. Verify the new upstream repository you've specified for your fork.

        $ git remote -v

   The output should look something like this:

        origin	git@github.com:your-user-name/uacalcsrc.git (fetch)
        origin	git@github.com:your-user-name/uacalcsrc.git (push)
        upstream	git@github.com:UACalc/uacalcsrc.git (fetch)
        upstream	git@github.com:UACalc/uacalcsrc.git (push)

   If the foregoing fails, try

        git remote add upstream https://github.com/UACalc/uacalcsrc.git


3. In the working directory of your local project, fetch the branches and their
   respective commits from the upstream repository.

        git fetch upstream

4. Check out your fork's local master branch.

        git checkout master

5. Merge the changes from upstream/master into your local master branch. This
   brings your fork's master branch into sync with the upstream repository,
   without losing your local changes. 

        git merge upstream/master

6. Finally, do

        git commit -m "merged changes from upstream"
        git push origin master

   and check that the GitHub page for your fork's repo shows the message,
   "This branch is even with UACalc:master." 


## History

This git repository was initially created on 2014 Nov 25 by importing Ralph
Freese's uacalcsrc cvs repository from sourceforge using the following command:

    git cvsimport -C ~/git/uacalcsrc -r cvs -k -v -d :pserver:anonymous@uacalc.cvs.sourceforge.net:/cvsroot/uacalc -A authorfile.txt uacalcsrc

Before issuing the above `git cvsimport` command the git-cvs package must be
installed (e.g., `sudo apt-get install git-cvs`)  

The authorfile.txt contains names and email addresses of authors who
contributed to the cvs source tree. This is needed in order to preserve the
contribution in the resulting git repo.


## Citing UACalc

If you are using BibTeX, you can use the following BibTeX entry to cite UACalc:

    @misc{UACalc,
      author =      {Ralph Freese and Emil Kiss and Matthew Valeriote},
      title =       {Universal {A}lgebra {C}alculator},
      note =        {Available at: {\verb+www.uacalc.org+}},
      year =        {2011},
    }

