


a = Extendable.new;

a.pingolino = { |self, x, y, z| [x, y, z].postln; 999 };

a.pingolino(1, 2, 3)

a.totalFree = { 1.0.rand } // exists, should throw






a = ExtendableObject(8);

a + 9;

a.zeta = { |self, y = 1| self.source * y };

a.zeta

a.zeta(10);

a.totalFree = { 1.0.rand }; // exists, should throw

a.addMethod('+', { pi });

a + 6 == pi


a = Halo2(2);

a + 4

4 + a // should this work?

a.addHalo(\data, { "yes" });

a.getHalo(\data);
