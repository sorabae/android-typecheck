  function testcase() 
  {
    var arr = new Array(10);
    try
{      arr.map(new Object());}
    catch (e)
{      if (e instanceof TypeError)
        return true;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  