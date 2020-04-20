git add --all
set /P t=Enter commit text: 
git commit -m "%t%"
git status
git push -u origin master