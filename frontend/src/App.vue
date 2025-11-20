<script>
	export default {
		onLaunch: function() {
			console.log('App Launch')
		},
		onShow: function() {
			console.log('App Show')
		},
		onHide: function() {
			console.log('App Hide')
		}
	}
</script>

<style>
	/* Global Styles */
    :root {
        --primary: #1E9E8D; /* 青绿主色 */
        --dark: #1F2937;
        --light-glass: rgba(255, 255, 255, 0.85); /* 高透白底 */
        --glass-border: rgba(255, 255, 255, 0.6);
    }
    
    body {
        background-color: #f5f5f2;
        /* Use system serif fonts for a traditional feel */
        font-family: "Songti SC", "SimSun", "Times New Roman", serif;
        margin: 0;
        color: var(--dark);
    }

    /* Utility Classes mimicking Tailwind/Prototype */
    .text-center { text-align: center; }
    .flex { display: flex; }
    .flex-col { flex-direction: column; }
    .items-center { align-items: center; }
    .justify-center { justify-content: center; }
    .justify-between { justify-content: space-between; }
    .relative { position: relative; }
    .absolute { position: absolute; }
    .w-full { width: 100%; }
    .h-full { height: 100%; }
    .hidden { display: none; }

    /* Opacity Utilities */
    .bg-white-40 { background-color: rgba(255, 255, 255, 0.4); }
    .bg-white-50 { background-color: rgba(255, 255, 255, 0.5); }
    .border-white-40 { border-color: rgba(255, 255, 255, 0.4); }
    .border-gray-300-30 { border-color: rgba(209, 213, 219, 0.3); }
    .bg-teal-50-50 { background-color: rgba(240, 253, 250, 0.5); }
    .bg-gradient-mask {
        background: linear-gradient(to top, #ffffff 0%, rgba(255, 255, 255, 0.9) 40%, transparent 100%);
    }
    
    /* Custom Components */
    .glass-card {
        background: var(--light-glass);
        backdrop-filter: blur(12px);
        -webkit-backdrop-filter: blur(12px);
        border: 1px solid var(--glass-border);
        box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.1);
        border-radius: 16px;
    }

    .btn-primary {
        background: linear-gradient(135deg, #1E9E8D 0%, #147D6F 100%);
        color: white;
        box-shadow: 0 4px 15px rgba(30, 158, 141, 0.4);
        transition: transform 0.2s;
        border: none;
        outline: none;
    }
    .btn-primary:active {
        transform: scale(0.98);
    }

    /* Background Animation */
    .ink-bg-anim {
        animation: ink-breathe 10s infinite ease-in-out;
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        z-index: -2;
        background-size: cover;
        background-position: center center;
        background-repeat: no-repeat;
        animation: ink-breathe 12s ease-in-out infinite alternate;
        opacity: 0.6;
    }

    .bg-overlay {
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        z-index: -1;
        background: linear-gradient(to bottom, 
            rgba(255,255,255,0.7) 0%, 
            rgba(255,255,255,0.5) 40%, 
            rgba(255,255,255,0.2) 100%);
        pointer-events: none; 
    }

    @keyframes ink-breathe {
        0% { opacity: 0.4; transform: scale(1.0); }
        100% { opacity: 0.7; transform: scale(1.05); }
    }
</style>