'''
Created on Aug 31, 2017

@author: nhamid
'''

indentAmount = 2


def describe(thing, indent = 0):
    spaces = ' ' * indent
    
    if type(thing) in [int, float, str]:
        return '*'
    if type(thing) is int:
        return 'number'
    elif type(thing) is float:
        return 'number'
    elif type(thing) is str:
        return 'string'
    elif isinstance(thing, list):
        if len(thing) is 0:
            return 'empty list'
        else:
            elts = describe(thing[0], incrInd(indent))
            if elts.count("\n") == 0:
                return 'list of ' + elts.strip()
            else:
                return 'list of:\n' + elts
    elif isinstance(thing, dict):
        elts = { k : describe(v, incrInd(indent)) for k, v in thing.items() }
        keys = [ k for k in thing.keys() if k ]
        keys.sort(key=lambda x: x.lower())
        desc = spaces + 'dictionary with {\n'
        key_spaces = ' ' * incrInd(indent)
        for k in keys:
            leader = key_spaces + k + " : "
            desc += leader + describe(thing[k], incrInd(len(leader))).strip() + "\n"
        #desc += str(elts) + "\n" + str(keys) + "\n"
        desc += spaces + "}"
    
        return desc
    
    return "*"



def incrInd(amt):
    return amt + indentAmount


if __name__ == "__main__":
    print(describe({ 'name' : 'blah', 'age' : 4, 'city' : 'Rome'}))
    print(describe({ 'name' : 'blah', 'age' : 4, 'cities' : ['Rome', 'Madrid', 'Tokyo']}))
    print(describe({ 'name' : { 'first' : "john", 'last' : 'doe'}, 'age' : 4, 'cities' : ['Rome', 'Madrid', 'Tokyo']}))
    
