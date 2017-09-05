'''
Created on Aug 31, 2017

@author: nhamid
'''
import unittest
from describe import describe

class Test(unittest.TestCase):


    def testDescribe(self):
        assert describe(5) == '*'
        assert describe(5.5) == '*'
        assert describe("5") == '*'
        assert describe([]) == 'empty list'
        assert describe([5, 2, 10]) == 'list of *'
        assert describe([ [ "hi", "bye"], ["here", "there"]]) == 'list of list of *'
        assert describe({ 'name' : 'blah', 'age' : 4, 'city' : 'Rome'}) == \
                'dictionary with {\n  age : *\n  city : *\n  name : *\n}'

        assert describe({ 'name' : 'blah', 'age' : 4, 'cities' : ['Rome', 'Madrid', 'Tokyo']}) == \
                'dictionary with {\n  age : *\n  cities : list of *\n  name : *\n}'



if __name__ == "__main__":
    #import sys;sys.argv = ['', 'Test.testName']
    unittest.main()