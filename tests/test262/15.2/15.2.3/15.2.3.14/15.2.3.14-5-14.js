  function testcase() 
  {
    var obj = [1, , 3, , 5, ];
    Object.defineProperty(obj, "10000", {
      get : (function () 
      {
        return "ElementWithLargeIndex";
      }),
      enumerable : true,
      configurable : true
    });
    var arr = Object.keys(obj);
    for(var p in arr)
    {
      if (arr[p] === "10000")
      {
        return true;
      }
    }
    return false;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  