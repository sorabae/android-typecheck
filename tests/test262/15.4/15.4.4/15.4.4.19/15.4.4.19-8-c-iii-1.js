  function testcase() 
  {
    function callbackfn(val, idx, obj) 
    {
      if (val % 2)
        return (2 * val + 1);
      else
        return (val / 2);
    }
    var srcArr = [0, 1, 2, 3, 4, ];
    var resArr = srcArr.map(callbackfn);
    if (resArr.length > 0)
    {
      var desc = Object.getOwnPropertyDescriptor(resArr, 1);
      if (desc.value === 3 && desc.writable === true && desc.enumerable === true && desc.configurable === true)
      {
        return true;
      }
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  