  function testcase() 
  {
    function callbackfn(val, idx, obj) 
    {
      return obj instanceof String;
    }
    var newArr = Array.prototype.filter.call("abc", callbackfn);
    return newArr[0] === "a";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  