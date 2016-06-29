#!/bin/bash

################################################################################
#    Copyright (c) 2016, KAIST.
#    All rights reserved.
#
#    Use is subject to license terms.
#
#    This distribution may include materials developed by third parties.
################################################################################

export WKSPACE=$SAFE_HOME/tests
export JSSPACE=$WKSPACE/js
export RSSPACE=$WKSPACE/result

cd $RSSPACE
rm -f success/astRewrite/*.test

cd $WKSPACE
succ_files=`find js/success -name "*.js" -print`

for fil in $succ_files
do
  prename=`basename $fil`
  name=${prename%.js}
  ast_out=$RSSPACE/success/astRewrite/$name.test

  echo "create $ast_out"
  safe astRewrite -astRewrite:out=$ast_out $fil
done
