  function testcase() 
  {
    function callbackfn(val, idx, obj) 
    {
      return obj instanceof Date;
    }
    var obj = new Date();
    obj.length = 1;
    obj[0] = 1;
    var newArr = Array.prototype.filter.call(obj, callbackfn);
    return newArr[0] === 1;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  