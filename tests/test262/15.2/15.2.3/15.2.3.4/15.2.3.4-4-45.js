  function testcase() 
  {
    try
{      var arr = [0, 1, 2, ];
      Array.prototype.protoProperty = "protoArray";
      var result = Object.getOwnPropertyNames(arr);
      for(var p in result)
      {
        if (result[p] === "protoProperty")
        {
          return false;
        }
      }
      return true;}
    finally
{      delete Array.prototype.protoProperty;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  