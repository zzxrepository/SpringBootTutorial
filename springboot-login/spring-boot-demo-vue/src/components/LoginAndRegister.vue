

<template>

  <div class="container right-panel-active">
   <!-- Sign Up -->
   <div class="container__form container--signup">
     <form action="#" class="form" id="form1">
       <h2 class="form__title">Sign Up</h2>
       <input type="text" placeholder="用户名" class="input" required />
       <input type="email" placeholder="邮箱" class="input" required />
       <input type="password" placeholder="密码" class="input" required />
       <button class="btn">注册</button>
     </form>
   </div>

   <!-- Sign In -->
   <div class="container__form container--signin">
     <form action="#" class="form" id="form2">
       <h2 class="form__title">Sign In</h2>
       <input type="email" placeholder="邮箱" class="input" required />
       <input type="password" placeholder="密码" class="input" required />
       <a href="#" class="link">忘记密码？</a>
       <button class="btn">登录</button>
     </form>
   </div>

   <!-- Overlay -->
   <div class="container__overlay">
     <div class="overlay">
       <div class="overlay__panel overlay--left">
         <button class="btn" id="signIn">登录</button>
       </div>
       <div class="overlay__panel overlay--right">
         <button class="btn" id="signUp">注册</button>
       </div>
     </div>
   </div>
 </div>

</template>
<script setup>
import { ref, onMounted } from 'vue'

defineProps({
 msg: String,
})

const count = ref(0)

onMounted(() => {
 const signInBtn = document.getElementById("signIn");
 const signUpBtn = document.getElementById("signUp");
 const firstForm = document.getElementById("form1");
 const secondForm = document.getElementById("form2");
 const container = document.querySelector(".container");

 signInBtn.addEventListener("click", () => {
   container.classList.remove("right-panel-active");
 });

 signUpBtn.addEventListener("click", () => {
   container.classList.add("right-panel-active");
 });

 firstForm.addEventListener("submit", (e) => {
   e.preventDefault();
   // 在这里处理注册逻辑
   alert("注册成功！");
 });

 secondForm.addEventListener("submit", (e) => {
   e.preventDefault();
   // 在这里处理登录逻辑
   alert("登录成功！");
 });
});
</script>
<style scoped>

   #app {
     align-items: center;
     background-color: var(--white);
     background: url("./bgimg3.jpg") no-repeat center center fixed;
     background-size: cover;
     display: grid;
     height: 100vh;
     place-items: center;
     margin: 0;
   }

   .form__title {
     font-weight: 300;
     margin: 0;
     margin-bottom: 1.25rem;
   }

   .link {
     color: var(--gray);
     font-size: 0.9rem;
     margin: 1.5rem 0;
     text-decoration: none;
   }

   .container {
     background-color: var(--white);
     border-radius: var(--button-radius);
     box-shadow: 0 0.9rem 1.7rem rgba(0, 0, 0, 0.25),
       0 0.7rem 0.7rem rgba(0, 0, 0, 0.22);
     height: var(--max-height);
     max-width: var(--max-width);
     overflow: hidden;
     position: relative;
     width: 100%;
     display: inline-block;
     margin-top: 20vh
   }

   .container__form {
     height: 100%;
     position: absolute;
     top: 0;
     transition: all 0.6s ease-in-out;
   }

   .container--signin {
     left: 0;
     width: 50%;
     z-index: 2;
   }

   .container.right-panel-active .container--signin {
     transform: translateX(100%);
   }

   .container--signup {
     left: 0;
     opacity: 0;
     width: 50%;
     z-index: 1;
   }

   .container.right-panel-active .container--signup {
     animation: show 0.6s;
     opacity: 1;
     transform: translateX(100%);
     z-index: 5;
   }

   .container__overlay {
     height: 100%;
     left: 50%;
     overflow: hidden;
     position: absolute;
     top: 0;
     transition: transform 0.6s ease-in-out;
     width: 50%;
     z-index: 100;
   }

   .container.right-panel-active .container__overlay {
     transform: translateX(-100%);
   }

   .overlay {
     background-color: var(--lightblue);
     background: url("./bgimg3.jpg") no-repeat center center fixed;
     background-size: cover;
     height: 100%;
     left: -100%;
     position: relative;
     transform: translateX(0);
     transition: transform 0.6s ease-in-out;
     width: 200%;
   }

   .container.right-panel-active .overlay {
     transform: translateX(50%);
   }

   .overlay__panel {
     align-items: center;
     display: flex;
     flex-direction: column;
     height: 100%;
     justify-content: center;
     position: absolute;
     text-align: center;
     top: 0;
     transform: translateX(0);
     transition: transform 0.6s ease-in-out;
     width: 50%;
   }

   .overlay--left {
     transform: translateX(-20%);
   }

   .container.right-panel-active .overlay--left {
     transform: translateX(0);
   }

   .overlay--right {
     right: 0;
     transform: translateX(0);
   }

   .container.right-panel-active .overlay--right {
     transform: translateX(20%);
   }

   .btn {
     background-color: var(--blue);
     background-image: linear-gradient(90deg, var(--blue) 0%, var(--lightblue) 74%);
     border-radius: 20px;
     border: 1px solid var(--blue);
     color: var(--white);
     cursor: pointer;
     font-size: 0.8rem;
     font-weight: bold;
     letter-spacing: 0.1rem;
     padding: 0.9rem 4rem;
     text-transform: uppercase;
     transition: transform 80ms ease-in;
   }

   .form > .btn {
     margin-top: 1.5rem;
   }

   .btn:active {
     transform: scale(0.95);
   }

   .btn:focus {
     outline: none;
   }

   .form {
     background-color: var(--white);
     display: flex;
     align-items: center;
     justify-content: center;
     flex-direction: column;
     padding: 0 3rem;
     height: 100%;
     text-align: center;
   }

   .input {
     background-color: #fff;
     border: none;
     padding: 0.9rem 0.9rem;
     margin: 0.5rem 0;
     width: 100%;
     border-bottom: 1px solid var(--gray);
     transition: border-bottom 0.3s;
   }

   .input:focus {
     outline: none;
     border-bottom: 2px solid var(--blue);
   }

   @keyframes show {
     0%, 49.99% {
       opacity: 0;
       z-index: 1;
     }
     50%, 100% {
       opacity: 1;
       z-index: 5;
     }
   }
</style>
