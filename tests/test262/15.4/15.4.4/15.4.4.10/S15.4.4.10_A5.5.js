  {
    var __result1 = Array.propertyIsEnumerable('slice') !== false;
    var __expect1 = false;
  }
  var result = true;
  for(var p in Array)
  {
    if (p === "slice")
    {
      result = false;
    }
  }
  {
    var __result2 = result !== true;
    var __expect2 = false;
  }
  