  function testcase() 
  {
    var result = false;
    function callbackfn(val, idx, obj) 
    {
      result = obj instanceof Boolean;
    }
    try
{      Boolean.prototype[0] = true;
      Boolean.prototype.length = 1;
      Array.prototype.forEach.call(false, callbackfn);
      return result;}
    finally
{      delete Boolean.prototype[0];
      delete Boolean.prototype.length;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  