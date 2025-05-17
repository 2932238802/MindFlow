<template>
  <div class="about-page-container">

    <!-- A 部分 -->
    <button class="return" id="return" @click="Return">返回</button>

    <header>
      <h1>{{ project.name }}</h1>
      <p class="tagline">{{ project.tagline }}</p>
    </header>

    <!-- A 部分 -->


    <!-- B 部分 -->
    <section id="about-project">
      <h2>关于本项目</h2>
      <p>{{ project.description }}</p>
    </section>
    <section id="features" v-if="project.features && project.features.length > 0">
      <h2>核心功能</h2>
      <ul>
        <li v-for="(feature, index) in project.features" :key="`feature-${index}`">{{ feature }}</li>
      </ul>
    </section>
    <!-- B 部分 -->


    <!-- C 部分 -->
    <section id="target-audience" v-if="project.target_audience">
      <h2>为谁而作</h2>
      <p>{{ project.target_audience }}</p>
    </section>
    <section id="technology" v-if="project.technology_stack && project.technology_stack.length > 0">
      <h2>技术栈</h2>
      <p v-if="project.tech_intro">{{ project.tech_intro }}</p>
      <ul>
        <li v-for="(tech, index) in project.technology_stack" :key="`tech-${index}`">{{ tech }}</li>
      </ul>
    </section>
    <!-- C 部分 -->


    <!-- D 部分 -->
    <section id="team-author" v-if="author.name || author.contact.email || author.contact.link">
      <h2>开发团队/作者</h2>
      <p v-if="author.bio">{{ author.bio }}</p>
      <p class="contact-info" v-if="author.contact.email || author.contact.link">
        如果你有任何问题、建议或反馈，欢迎通过
        <a v-if="author.contact.email" :href="`mailto:${author.contact.email}`">{{ author.contact.email }}</a>
        <span v-if="author.contact.email && author.contact.linkurl"> 或 </span>
        <a v-if="author.contact.linkurl" :href=author.contact.linkurl target="_blank" rel="noopener noreferrer">{{ author.contact.linkText || '项目主页' }}</a>
        联系我们。
      </p>
    </section>
    <!-- D 部分 -->


    <!-- E 部分 -->
    <footer>
      <p>&copy; {{ copyright_year }} {{ author.name || project.name }}</p>
    </footer>
    <!-- E 部分 -->

  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useRouter  } from 'vue-router';

const router = useRouter()

const Return = ()=>
{
    router.push({name:'index'})
}

const project = ref({
  name: "MindFLow",
  tagline: "打造免费的简单大气每日计划清单",
  description: "MindFLow 是一款坚持更新,采纳使用者意见,制作的一款免费简单大气的在线每日清单。",
  features: [
    "登陆注册信息保护",
    "每日计划的增删查改",
  ],
  target_audience: "本项目主要服务于 all people!",
  tech_intro: "本项目主要使用java ts vue html js css scss等进行的开发",
  technology_stack: [
    "openjdk 17.0.15",
    "vue 3.5.13",
    "vue-router ^4.5.0",
    "typescript ~5.7.2",
  ],
  version: "v0.1.0-alpha",

});

const author = ref({
  name: "林圣杰 尹心怡", 
  bio: "希望对大家有所帮助",
  contact: {
    email: "19857198709@163.com", 
    linkText: "MindFlow", 
    linkurl: "https://github.com/2932238802/MindFlow.git" 
  }
});
const copyright_year = computed(() => new Date().getFullYear());

</script>
<style scoped>
@import '@assets/about.css';

</style>

