  function testcase() 
  {
    try
{      Array.prototype.forEach.call(null);
      return false;}
    catch (e)
{      return (e instanceof TypeError);}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  