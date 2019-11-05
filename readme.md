DTX MANAGER
===========

A simple interactive tool to manipulate sections (segments) of Atari DOS II 2.0
binary load files.

Requires Java SE 8 or newer.

Originally, this was meant to be a non-public tool, however I decided to share
it. It is definitely rough, not polished, without undo, but does the job. The functions cover
what I personally needed.

List sections, remove section, change addresses of section, merge sections,
split section, replace section with LDA/STA code, add section that moves block,
replace RUN/INIT sections with code, create monolithic binary, copy section as C
array, add RUN/INIT, ...