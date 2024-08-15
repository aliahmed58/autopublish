echo 'Outputting FBService logs'

cat response.log | grep "^.*FBService.*$" > fb.log
