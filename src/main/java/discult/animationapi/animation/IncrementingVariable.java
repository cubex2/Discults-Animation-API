package discult.animationapi.animation;

public class IncrementingVariable {
   public float value = 0.0F;
   public float increment;
   public float limit;

   public IncrementingVariable(float increment, float limit) {
      this.increment = increment;
      this.limit = limit;
   }

   public void tick() {
      this.value += this.increment;
      if (this.value >= this.limit) {
         this.value = 0.0F;
      }

   }
}
