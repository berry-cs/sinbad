

import json, os, time, webbrowser
from collections import OrderedDict
from appdirs import user_data_dir

from tkinter import *
from tkinter.ttk import Separator


class PrefsGUI:
    
    def __init__(self, first_time = False):
        root = Tk()
        self.first_time = first_time
        self.root = root
        self.prefs = load_pref_file()
        self.share_usage = BooleanVar(value = self.prefs.get("share_usage", False))
        self.notify_updates = BooleanVar(value = self.prefs.get("notify_updates", False))

    def save_prefs(self, *args):
        self.prefs["share_usage"] = self.share_usage.get()
        self.prefs["notify_updates"] = self.notify_updates.get()
        write_pref_file(self.prefs)        
        self.root.destroy()

    def show_prefs_window(self):
        root = self.root
        root.title("Sinbad preferences")
        if self.first_time:
            root.protocol('WM_DELETE_WINDOW', lambda: False) 
        
        if self.first_time:
            info = Label(root, text="It looks like you've used the Sinbad library a few times now.\nPlease take a moment to adjust your preferences.", justify=LEFT)
        else:
            info = Label(root, text="Adjust your preferences", justify=LEFT)
        info.grid(row=0,columnspan=2,sticky=NW)
        Separator(root,orient=HORIZONTAL).grid(row=1, columnspan=2, sticky=EW)
        c1 = Checkbutton(root, text="Share usage & diagnostics information", variable=self.share_usage)
        c1.grid(row=2, column=0, sticky=W)
        m = Button(root, text="More info...", command=launch_usage_collection_info)
        m.grid(row=2, column=1)
        c2 = Checkbutton(root, text="Check and notify for updates", variable=self.notify_updates)
        c2.grid(row=3, columnspan=2, sticky=W)
        btn = Button(root, text="Save", default=ACTIVE, command=self.save_prefs)
        btn.grid(row=4, columnspan=2)
        root.attributes('-topmost', True)
        root.lift()
        btn.focus()
        root.bind("<Return>", self.save_prefs)
        root.mainloop()
        self.root = None

    
def launch_usage_collection_info(*args):
    webbrowser.open_new(get_pref("server_base") + "usage.php")
    
def pref_file_name():
    #prefs_file = os.path.expanduser("~{0}sinbad_prefs.txt".format(os.path.sep))
    prefs_dir = user_data_dir("Sinbad")
    if not os.path.isdir(prefs_dir):
        os.makedirs(prefs_dir, 0o777, True)
    if not os.path.isdir(prefs_dir):
        return None
    return prefs_dir + os.path.sep + "sinbad_py_prefs.txt"

def load_pref_file():
    prefs_file = pref_file_name()
    if os.path.isfile(prefs_file):
        with open(prefs_file) as fp:
            prefs = json.load(fp)
    else:
        prefs = default_prefs()
    #print(prefs)
    return prefs

def write_pref_file(prefs):
    prefs_file = pref_file_name()
    with open(prefs_file, 'w') as fp:
        json.dump(prefs, fp)
    
def default_prefs():
    ts = int(time.time())
    return OrderedDict([( "share_usage" , False ),
                        ( "notify_updates" , False),
                        ( "run_count" , 1),
                        ( "first_use_ts", ts),
                        ( "last_use_ts", ts),
                        ( "server_base" , "http://cs.berry.edu/sinbad/")])

def increment_run_count():
    prefs = load_pref_file()
    prefs["run_count"] = min(prefs.get("run_count", 0) + 1, 1000001)
    prefs["last_use_ts"] = int(time.time())    
    write_pref_file(prefs)

def get_pref(name):
    prefs = load_pref_file()
    return prefs.get(name)

def set_pref(name, value):
    prefs = load_pref_file()
    prefs[name] = value
    write_pref_file(prefs)

def preferences(**kw_args):
    PrefsGUI(**kw_args).show_prefs_window()



# increment_run_count()

# if get_pref("run_count") == 10:
#    set_pref("share_usage", True)
#    preferences(first_time = True)


