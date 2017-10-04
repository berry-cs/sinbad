
# Installation (Python)

## Requirements

The *Sinbad* library is intended for use with Python version 3.5 or later.

-----

Install using either a  [Python shell](#python-shell) or from a [terminal console](#terminal), or if all else fails, [manual download and installation](#manual-download-and-install).

## Python Shell

Type the following from the interactive Python shell (don't type the `>>>`s):

````
>>> import pip
>>> pip.main(['install', 'sinbad'])
````

## Terminal

From a console prompt, type the following:

````
pip3 install -U sinbad
````

If `pip3` does not work, use `pip`.


## Manual Download and Install

* Visit [https://pypi.python.org/pypi/Sinbad](https://pypi.python.org/pypi/Sinbad) in your browser.
* Click the "Download" button to get the latest `.tar.gz` file.
* Unpack the archive (type `tar xzvf sinbad...tar.gz` in a terminal).
* In a terminal, use `cd` to change your working directory to the location of the unpacked Sinbad directory.
* Type `python setup.py install`.



-------

## Uninstall

Type `pip uninstall sinbad` at the terminal prompt, or 

````
>>> import pip
>>> pip.main(['uninstall', 'sinbad'])
````

in an interactive Python shell.

