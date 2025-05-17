<template>
  <div class="container">

    <button class="return" id="return" @click="Return">返回</button>
    <!-- A -->
    <header>
      <h1>{{ page_title }}</h1>
      <p class="tagline">{{ tagline }}</p>
    </header>
    <!-- A -->


    <!-- B -->
    <section id="why-support">
      <h2>为什么支持 {{ project_name }}？</h2>
      <p>
        {{ project_name }} 是一个{{ project_type_description }} 
        我们致力于{{ project_goal }} 
      </p>
      <p>您的支持将帮助我们：</p>
      <ul>
        <li v-for="benefit in support_benefits" :key="benefit">{{ benefit }}</li>
      </ul>
    </section>
    <!-- B -->


    <!-- C -->
    <section id="ways-to-support">
      <h2>如何支持我们</h2>

      <h3>直接捐助</h3>
      <p>
        如果您觉得 {{ project_name }} 对您有所帮助，并希望支持我们的工作，可以通过以下方式进行小额捐助 每一份支持都是对我们的巨大鼓励！
      </p>
      <div class="donation-methods">
        <p v-if="wechatPayQr || alipayQr">
          <strong>通过支付二维码 (如果适用)</strong><br>
          您也可以通过扫描下方的二维码进行支持：
        </p>
        <div class="qr-codes">
            <div v-if="wechatPayQr" class="qr-code-item">
                <p>微信支付：</p>
                <img :src="wechatPayQr" alt="微信收款码">
            </div>
        </div>

      </div>

      <h3>合作与商业赞助</h3>
      <p>我们欢迎各种形式的合作与商业赞助 如果您或您的企业希望：</p>
      <ul>
        <li v-for="collaborationPoint in collaboration_points" :key="collaborationPoint">{{ collaborationPoint }}</li>
      </ul>
      <p>请通过以下方式联系我们，我们非常期待与您探讨合作的可能性 </p>

      <h3>宣传与分享</h3>
      <p>
        即使您目前无法提供资金支持，将 {{ project_name }} 分享给更多需要它的人，或者在社交媒体上提及我们，也是对我们极大的帮助！
      </p>
    </section>
    <!-- C -->



    <!-- D -->
    <section id="contact-for-sponsorship">
      <h2>联系方式</h2>
      <p>
        对于捐助、合作、商业赞助或任何其他相关事宜，欢迎通过以下方式联系 {{ contact_person_or_team }}：
      </p>
      <div class="contact-methods">
        <p><strong>电子邮件：</strong> <a :href="'mailto:' + email_address">{{ email_address }}</a></p>
        <p v-if="project_repolink"><strong>项目主页：</strong> <a :href="project_repolink" target="_blank">{{ project_repolink }}</a></p>
      </div>
      <p>我们会在收到消息后尽快回复您!</p>
    </section>
    <!-- D -->




    <!-- E -->
    <section id="acknowledgements" v-if="show_acknowledgements">
      <h2>鸣谢</h2>
      <p>
        我们在此衷心感谢所有已经支持和正在支持 {{ project_name }} 的朋友们！
        <a v-if="supporters_list_pagelink" :href="supporters_list_pagelink" target="_blank">查看赞助者名单</a>
      </p>
    </section>
    <footer>
      <p>&copy; {{ current_year }} {{ copyright_holder }} 您的支持意义非凡 </p>
    </footer>
    <!-- E -->
    
    
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';


const router = useRouter()
const page_title = ref('支持我们');
const tagline = ref('您的支持是我们持续创作与改进的动力！');
const project_name = ref('MindFLow');
const contact_person_or_team = ref('林圣杰 尹心怡 19857198709'); 
const project_type_description = ref('开源项目'); 
const project_goal = ref('为用户提供高效的计划清单');
const support_benefits = ref([
  '覆盖服务器和维护成本',
  '投入更多时间进行新功能开发和优化',
  '保持项目的独立性和可持续发展',
]);
const wechatPayQr = ref('../../public/mindflow.png'); 
const collaboration_points = ref([
  '在我们的项目/网站上进行品牌推广!',
  '定制开发特定功能!!',
  '寻求技术咨询或项目合作!!!',
  '成为项目的长期赞助伙伴!!!!',
]);
const email_address = ref('19857198709@163.com'); 
const project_repolink = ref('https://github.com/2932238802/MindFlow.git');
const show_acknowledgements = ref(true); 
const supporters_list_pagelink = ref(''); 
const copyright_holder = ref(project_name.value); 
const current_year = computed(() => new Date().getFullYear());

const Return = ()=>
{
    router.push({name:"index"})
}

</script>


<style scoped>
@import '@assets/sponsor.css';

</style>