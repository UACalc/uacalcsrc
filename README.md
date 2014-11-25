The main Universal Algebra Calculator website is http://uacalc.org

This git repository was initially created on 2014 Nov 25 by importing Ralph
Freese's uacalcsrc cvs repository from sourceforge using the following command:

    git cvsimport -C ~/git/uacalcsrc -r cvs -k -v -d :pserver:anonymous@uacalc.cvs.sourceforge.net:/cvsroot/uacalc -A authorfile.txt uacalcsrc

**Notes**

1. Before issuing the above `git cvsimport` command the git-cvs package must be
  installed (e.g., `sudo apt-get install git-cvs`)  

2. The authorfile.txt contains names and email addresses of authors who
contributed to the cvs source tree. This is needed in order to preserve the
contribution in the resulting git repo.


