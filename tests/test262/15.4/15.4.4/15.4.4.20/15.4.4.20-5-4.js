  function testcase() 
  {
    var res = false;
    function callbackfn(val, idx, obj) 
    {
      return this.res;
    }
    function foo() 
    {
      
    }
    foo.prototype.res = true;
    var f = new foo();
    var srcArr = [1, ];
    var resArr = srcArr.filter(callbackfn, f);
    if (resArr.length === 1)
      return true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  