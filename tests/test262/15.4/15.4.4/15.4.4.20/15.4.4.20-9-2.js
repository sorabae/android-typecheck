  function testcase() 
  {
    function callbackfn(val, idx, obj) 
    {
      srcArr[2] = - 1;
      srcArr[4] = - 1;
      if (val > 0)
        return true;
      else
        return false;
    }
    var srcArr = [1, 2, 3, 4, 5, ];
    var resArr = srcArr.filter(callbackfn);
    if (resArr.length === 3 && resArr[0] === 1 && resArr[2] === 4)
      return true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  