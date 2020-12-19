/*

This is a better intergrated implementation of the Halo class by Alberto de Campo in JITLibExtensions.
It allows us to add any extra information to objects

*/


// Halo2: hook properties to objects

a = Halo2(2);

a - 4 == -2

4 - a  == 2

a.addHalo(\data, { "yes" });

a.getHalo(\data);

a.getHalo(\data).value;


 // now this is still addHalo2 (not to collide with addHalo from JITLibExtensions)

a = 7.addHalo2(\data, 0.003);
a + 100;
a.getHalo(\data);
