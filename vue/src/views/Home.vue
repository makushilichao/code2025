<template>
    <div :class="{ 'dark-mode': isDark }">
        <div class="card" style="margin-left: -5px">你好同学，欢迎使用本系统</div>
        <div class="card" style="margin-top: 10px; width: 50%; float: left; margin-left: -5px">
            <div class="card-title" style="margin-bottom: 15px">
                <div class="card-title-text">
                    <div class="card-title-text-title">
                        <i class="el-icon-time"></i>
                        <span style="font-size: 20px">系统公告</span>
                    </div>
                </div>
            </div>
            <el-timeline style="max-width: 600px; height: 380px">
                <el-timeline-item :timestamp="item.createTime" color="#0bbd87" placement="top"
                                  v-for="item in data.noticeData">
                    <h4>{{ item.title }}</h4>
                    <p>{{ item.content }}</p>
                </el-timeline-item>
            </el-timeline>
        </div>

        <div class="card" style="margin-top: 10px; width: 50%; float: right">
            <div class="card-title" style="margin-bottom: 15px">
                <div class="card-title-text">
                    <div class="card-title-text-title">
                        <i class="el-icon-time"></i>
                        <span style="font-size: 20px">热点名词</span>
                    </div>
                </div>
            </div>
            <div id="pie" style="width: 100%; height: 380px;"></div>
        </div>

        <div class="card" style="margin-top: 10px; width: 50%; float: left; margin-left: -5px">
            <div class="card-title" style="margin-bottom: 15px">
                <div class="card-title-text">
                    <div class="card-title-text-title">
                        <i class="el-icon-time"></i>
                        <span style="font-size: 20px">数量排行</span>
                    </div>
                </div>
            </div>
            <div id="bar" style="width: 100%; height: 380px;"></div>
        </div>

        <div class="card" style="margin-top: 10px; width: 50%; float: right">
            <div class="card-title" style="margin-bottom: 15px">
                <div class="card-title-text">
                    <div class="card-title-text-title">
                        <i class="el-icon-time"></i>
                        <span style="font-size: 20px">数量排行</span>
                    </div>
                </div>
            </div>
            <div id="line" style="width: 100%; height: 380px;"></div>
        </div>
    </div>
</template>

<script setup>
import {reactive, onMounted} from "vue";
import request from "../../utils/request.js";
import {ElMessage} from "element-plus";
import * as echarts from "echarts";
import {useDark, useToggle} from '@vueuse/core'

const isDark = useDark()
const toggleDark = useToggle(isDark)

const data = reactive({
    user: JSON.parse(localStorage.getItem('code_user') || '{}'),
    noticeData: [],
    form: {}
});

const getNotice = () => {
    request.get('/notice/selectAll').then(res => {
        if (res.code === '200') {
            data.noticeData = res.data.slice(0, 3);
        } else {
            ElMessage.error(res.msg);
        }
    });
};

const loadPie = () => {
    request.get('/echarts/pie').then(res => {
        if (res.code === '200') {
            let chartDom = document.getElementById('pie');
            let myChart = echarts.init(chartDom);
            optionPie.series[0].data = res.data;
            myChart.setOption(optionPie);
        } else {
            ElMessage.error(res.msg);
        }
    })
};

const loadBar = () => {
    request.get('/echarts/bar').then(res => {
        if (res.code === '200') {
            console.log(res.data)
            let chartDom = document.getElementById('bar');
            let myChart = echarts.init(chartDom);
            optionBar.xAxis.data = res.data.xAxis;
            optionBar.series[0].data = res.data.yAxis;
            myChart.setOption(optionBar);
        } else {
            ElMessage.error(res.msg);
        }
    })
};

const loadLine = () => {
    request.get('/echarts/line').then(res => {
        if (res.code === '200') {
            console.log(res.data)
            let chartDom = document.getElementById('line');
            let myChart = echarts.init(chartDom);
            optionLine.xAxis.data = res.data.xAxis;
            optionLine.series[0].data = res.data.yAxis;
            myChart.setOption(optionLine);
        } else {
            ElMessage.error(res.msg);
        }
    })
};

onMounted(() => {
    loadPie();
    loadBar();
    loadLine()
});

let optionPie = {
    title: {
        text: '不同分类下用户发布论坛文章的数量',
        subtext: '统计维度：文章分类',
        left: 'center'
    },
    tooltip: {
        trigger: 'item'
    },
    legend: {
        orient: 'vertical',
        left: 'left'
    },
    series: [
        {
            name: '数量占比',
            type: 'pie',
            radius: ['40%', '70%'],
            data: [],
            padAngle: 5,
            avoidLabelOverlap: false,
            itemStyle: {
                borderRadius: 10
            },
            label: {
                show: false,
                position: 'center'
            },
            emphasis: {
                label: {
                    show: true,
                    fontSize: 40,
                    fontWeight: 'bold'
                }
            },
            labelLine: {
                show: false
            },
        }
    ]
};

let optionBar = {
    title: {
        text: '不同用户发布论坛文章数量Top5',
        subtext: '统计维度：用户昵称',
        left: 'center'
    },
    tooltip: {
        trigger: 'item'
    },
    grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
    },
    xAxis: {
        type: 'category',
        data: [],
        axisTick: {
            alignWithLabel: true
        }
    },
    yAxis: {
        name: '文章数量',
        type: 'value'
    },
    series: [
        {
            data: [],
            type: 'bar',
            showBackground: true,
            backgroundStyle: {
                color: 'rgba(180, 180, 180, 0.2)'
            }
        }
    ]
};

let optionLine = {
    title: {
        text: '最近一周每天用户发布文章数量',
        subtext: '统计维度：最近一周',
        left: 'center'
    },
    tooltip: {
        trigger: 'item'
    },
    xAxis: {
        name: '日期',
        type: 'category',
        data: [],
        axisTick: {
            alignWithLabel: true
        }
    },
    yAxis: {
        name: '文章数量',
        type: 'value'
    },
    series: [
        {
            data: [],
            type: 'line',
            smooth: true,
            markPoint: {
                data: [
                    {type: 'max', name: '最大值'},
                    {type: 'min', name: '最小值'}
                ]
            },
            markLine: {
                data: [
                    {type: 'average', name: '平均值'}
                ]
            }
        }
    ]
};

getNotice();
</script>