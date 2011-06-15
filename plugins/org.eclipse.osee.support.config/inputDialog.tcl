#!/bin/sh
# Tcl ignores the next line -*- tcl -*- \
exec wish "$0" -- "$@"
 
package require Tk

set ::_inputTitle [lindex $argv 0]
set ::_promptText [lindex $argv 1]

wm title . $::_inputTitle
#wm geometry . 200x100

# change the following to 'wm resizable . 0 0' to prevent resizing
wm resizable . 0 0
wm protocol . WM_DELETE_WINDOW {set _res {}}

grid [ttk::frame .c -padding "3 3 12 12"] -column 0 -row 0 
grid columnconfigure . 0 -weight 1; 
grid rowconfigure . 0 -weight 1


grid [ttk::label .c.message -textvar _promptText] -row 0 -column 1 

if { [string equal $_promptText "Enter Password"] } {
   grid [ttk::entry .c.textField  -show * -textvar _password] -row 0 -column 2
} else {
   grid [ttk::entry .c.textField  -textvar _password] -row 0 -column 2
}
grid [ttk::button .c.ok -text "OK" -command onOk] -row 1 -column 1 
grid [ttk::button .c.cancel -text "Cancel" -command onCancel] -row 1 -column 2 


foreach w [winfo children .c] {grid configure $w -padx 5 -pady 5}

focus .c.textField

bind . <Return> [list .c.ok invoke]
bind . <Escape> [list .c.cancel invoke]
bind . <Configure> {center_the_toplevel %W}
raise .
grab set .

proc onOk {} {  
   set ::_res [list $::_password]
   puts $::_res
}

proc onCancel {} {  
   set ::_res {}
}

proc center_the_toplevel { w } {
   if { [string equal $w [winfo toplevel $w]] } {
      set width 250
      set height 80
      set x [expr { ( [winfo vrootwidth  $w] - $width  ) / 2 }]
      set y [expr { ( [winfo vrootheight $w] - $height ) / 2 }]
      
      wm geometry $w ${width}x${height}+${x}+${y}

      bind $w <Configure> {}
   }

   return
}

vwait _res
destroy .

return $::_res