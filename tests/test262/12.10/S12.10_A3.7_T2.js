  this.p1 = 1;
  var result = "result";
  var myObj = {
    p1 : 'a',
    value : 'myObj_value',
    valueOf : (function () 
    {
      return 'obj_valueOf';
    })
  };
  with (myObj)
  {
    result = (function () 
    {
      p1 = 'x1';
      return value;
    })();
  }
  {
    var __result1 = p1 !== 1;
    var __expect1 = false;
  }
  {
    var __result2 = result !== "myObj_value";
    var __expect2 = false;
  }
  {
    var __result3 = myObj.p1 !== "x1";
    var __expect3 = false;
  }
  