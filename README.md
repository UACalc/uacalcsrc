The main Universal Algebra Calculator website is http://uacalc.org

This git repository was initially created on 2014 Nov 25 by importing Ralph
Freese's uacalcsrc cvs repository from sourceforge using the following command:

    git cvsimport -C ~/git/uacalcsrc -r cvs -k -v -d :pserver:anonymous@uacalc.cvs.sourceforge.net:/cvsroot/uacalc -A authorfile.txt uacalcsrc

**General Notes**

1. Before issuing the above `git cvsimport` command the git-cvs package must be
  installed (e.g., `sudo apt-get install git-cvs`)  

2. The authorfile.txt contains names and email addresses of authors who
contributed to the cvs source tree. This is needed in order to preserve the
contribution in the resulting git repo.

**Eclipse Notes**
To import the source code in Eclipse, follow these steps:

1. First clone the repository to your local drive with something like

        git clone https://github.com/UACalc/uacalcsrc.git ~/git/uacalcsrc

2. Open Eclipse and use the File menu to import the source code:

        File --> Import --> Git --> Projects from Git

   then click Next.

3. Select `Local`, click Next, then click Add and browse to the directory where
   you clone the repository in Step 1 above (e.g., ~/git/uacalcsrc).

4. Select the uacalcsrc repository and click Next.

5. Select the `Import existing project` radio button, click Next, and then
   select the algebra project and click finish.

