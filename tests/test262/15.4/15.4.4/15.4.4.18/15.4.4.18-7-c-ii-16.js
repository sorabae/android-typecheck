  function testcase() 
  {
    var result = false;
    function callbackfn(val, idx, obj) 
    {
      result = (this.valueOf() !== false);
    }
    var obj = {
      0 : 11,
      length : 2
    };
    Array.prototype.forEach.call(obj, callbackfn, false);
    return ! result;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  
