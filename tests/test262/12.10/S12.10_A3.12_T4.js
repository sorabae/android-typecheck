  this.p1 = 1;
  var result = "result";
  var value = "value";
  var myObj = {
    p1 : 'a',
    value : 'myObj_value',
    valueOf : (function () 
    {
      return 'obj_valueOf';
    })
  };
  try
{    with (myObj)
    {
      var f = (function () 
      {
        p1 = 'x1';
        throw value;
      });
    }
    f();}
  catch (e)
{    result = e;}

  {
    var __result1 = ! (p1 === 1);
    var __expect1 = false;
  }
  {
    var __result2 = ! (myObj.p1 === "x1");
    var __expect2 = false;
  }
  {
    var __result3 = ! (result === "myObj_value");
    var __expect3 = false;
  }
  