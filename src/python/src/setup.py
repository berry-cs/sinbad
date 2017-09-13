from distutils.core import setup

setup(
    name='Sinbad',
    version='0.1.0',
    author='Nadeem Abdul Hamid',
    author_email='nadeem@acm.org',
    packages=['sinbad', 'sinbad.test'],
#    scripts=[],
    url='http://cs.berry.edu/sinbad/',
    license='LICENSE.txt',
    description='Automated structure inference and binding of data',
    long_description=open('README.txt').read(),
    python_requires='~=3.5',
    install_requires=[
           "jsonpath_rw >= 1.4.0",
           "satori-rtm-sdk >= 1.4.0",
           "xmltodict >= 0.11.0",
           "appdirs >= 1.4.3",
    ],
    classifiers=[
        # How mature is this project? Common values are
        #   3 - Alpha
        #   4 - Beta
        #   5 - Production/Stable
        'Development Status :: 3 - Alpha',
    
        # Indicate who your project is intended for
        'Intended Audience :: Education',
        'Topic :: Software Development :: Libraries',
    
        # Pick your license as you wish (should match "license" above)
         'License :: OSI Approved :: MIT License',
    
        # Specify the Python versions you support here. In particular, ensure
        # that you indicate whether you support Python 2, Python 3 or both.
        'Programming Language :: Python :: 3.5',
        'Programming Language :: Python :: 3.6',
    ],
      
)
