
from sinbad.datasource import DataSource
from sinbad import prefs




prefs.increment_run_count()
if prefs.get_pref("run_count") == 10:
    prefs.set_pref("share_usage", True)
    prefs.preferences(first_time = True)

