  function testcase() 
  {
    try
{      Object.freeze(undefined);
      return false;}
    catch (e)
{      return e instanceof TypeError;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  