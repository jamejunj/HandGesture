git add --all
set /P t=Enter commit text: 
git commit -m "%t%"
git push -u origin master